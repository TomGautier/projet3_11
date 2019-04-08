import { injectable, inject } from "inversify";
import { SocketService } from "./socket.service";
import { DrawingSessionService } from "./drawingSession.service";
import { TYPES } from "../types";
import SocketEvents from "../../../common/communication/socketEvents";
import { UserManager } from "./user.manager";
import Image from "../schemas/image";
import { DatabaseService } from "./database.service";

@injectable()
export class DrawingSessionManager {
    /*If it is selected: the key is the object id and the value is the user's sessionId who selected it.
    Otherwise, it doesn't exist.*/
    private selectedObjects: Map<String, String> = new Map();
    // Key: username, value: imageId
    private connectedUsers : Map<String, String[]> = new Map();
    
    constructor(@inject(TYPES.SocketService) private socketService: SocketService,
                @inject(TYPES.DatabaseService) private databaseService: DatabaseService,
                @inject(TYPES.DrawingSessionServiceInterface) private drawingSessionService: DrawingSessionService,
                @inject(TYPES.UserManager) private userManager: UserManager)
               { 
        // args[0] contains the socket id, args[1][0] the drawing session id.
        this.socketService.subscribe(SocketEvents.JoinDrawingSession, args => this.joinSession(args[0], JSON.parse(args[1][0])));
        this.socketService.subscribe(SocketEvents.LeaveDrawingSession, args => this.leaveSession(args[0], JSON.parse(args[1][0])));
        // args[0] contains the socket id, args[1] is a json with the session id, username and properties of the object.
        this.socketService.subscribe(SocketEvents.AddElement, args => this.addElement(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], JSON.parse(args[1][0]), this.addElement));
        this.socketService.subscribe(SocketEvents.DeleteElements, args => this.deleteElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.deleteElements));
        this.socketService.subscribe(SocketEvents.ModifyElement, args =>this.modifyElement(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));
        
        this.socketService.subscribe(SocketEvents.DuplicateElements, args => this.duplicateElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], JSON.parse(args[1][0]), this.addElement));
        this.socketService.subscribe(SocketEvents.CutElements, args => this.cutElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.deleteElements));
        this.socketService.subscribe(SocketEvents.DuplicateCutElements, args => this.duplicateCutElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], JSON.parse(args[1][0]), this.addElement));

        
        this.socketService.subscribe(SocketEvents.StackElement, args =>this.stackElements(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));
        this.socketService.subscribe(SocketEvents.UnstackElement, args =>this.unstackElements(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));

        this.socketService.subscribe(SocketEvents.SelectElements, args => this.selectElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.selectElements));
        //this.socketService.subscribe(SocketEvents.NewUserJoined, args => this.verifyAndAct(args[0], args[1][0], this.resizeCanvas));
        this.socketService.subscribe(SocketEvents.ResizeCanvas, args => this.resizeCanvas(JSON.parse(args[1][0])));

        this.socketService.subscribe(SocketEvents.ResetCanvas, args => this.resetCanvas(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.resetCanvas));
        
    }

    public joinSession(socketId: string, doc : any) {
        console.log('Socket', socketId, 'joined session', doc.imageId);
        this.socketService.joinRoom(doc.imageId, socketId);
        this.newUserJoined(doc);    
        this.socketService.emit(socketId, SocketEvents.JoinedDrawingSession, doc); 
    }

    public newUserJoined(doc : any) {
        if (this.connectedUsers.get(doc.imageId) == undefined){
            this.connectedUsers.set(doc.imageId, new Array<String>());
        }
        var users = this.connectedUsers.get(doc.imageId) as String[];
        if (users !== undefined){
            if (doc.username != null && users.indexOf(doc.username) == -1){
                users.push(doc.username);
            }
            this.socketService.emit(doc.imageId,SocketEvents.NewUserJoined, users);
        } 
    }

    public leaveSession(socketId: string, doc: any) {
        this.selectedObjects.forEach((value: String, key: String, map) =>
        {
            // TODO: Delete every user's selected objects when he leaves a session.
            console.log(value, key);
            map.delete(key);
        });

        this.socketService.leaveRoom(doc.imageId, socketId);

        var users = this.connectedUsers.get(doc.imageId) as String[];
        
        const index = users.indexOf(doc.username, 0);
        if (index > -1) {
            users.splice(index, 1);
        }

        this.connectedUsers.set(doc.imageId, users);
        this.socketService.emit(doc.sessionId, SocketEvents.LeftDrawingSession, users);
    }

    // doc should be structured as a Shape. See: /schemas/shape.ts
    public addElement(doc: any) {
        console.log(doc);
        this.drawingSessionService.addElement(doc.shape.id,doc.shape.imageId, doc.shape.author, doc.shape.properties)
        .catch(err => {
            console.log('addElement:', err)
        });
        this.socketService.emit(doc.shape.imageId, SocketEvents.AddedElement,doc);
    }

    // doc.elementIds should be an array containing the IDs of the shapes to delete.
    public deleteElements(doc: any) {
        this.drawingSessionService.deleteElements(doc.elementIds)
        .catch(err => {
            console.log('deleteElements:', err)
        });
        this.socketService.emit(doc.imageId, SocketEvents.DeletedElements, doc);
    }

    // doc should be structured as a Shape. See: /schemas/shape.ts
    public modifyElement(doc: any) {
        console.log('modify', doc);
        for (const shape of doc.shapes){
            this.drawingSessionService.modifyElement(shape)
            .catch(err => {
                console.log('modifyElement:', err)
            });
        }
        this.socketService.emit(doc.imageId, SocketEvents.ModifiedElement, doc);
    }

    public selectElements(doc : any) {
        console.log("select", doc);
        this.socketService.emit(doc.imageId, SocketEvents.SelectedElements, doc);
    }

    public duplicateElements(doc: any) {
        console.log('duplicate', doc);        
        for (const shape of doc.shapes){
         this.drawingSessionService.addElement(shape.id, shape.imageId, shape.author, shape.properties)
         .catch(err => {
            console.log('duplicateElements:', err)
        });
        }
       this.socketService.emit(doc.shapes[0].imageId, SocketEvents.DuplicatedElements, doc);
    }

    public cutElements(doc: any) {
        console.log('cut', doc);
        this.drawingSessionService.deleteElements(doc.elementIds)
        .catch(err => {
            console.log('cutElements:', err)
        });
        this.socketService.emit(doc.imageId, SocketEvents.CutedElements, doc);
    }
    public duplicateCutElements(doc : any){
        console.log('duplicateCut', doc);
        for (const shape of doc.shapes){
            this.drawingSessionService.addElement(shape.id, shape.imageId, shape.author, shape.properties)
            .catch(err => {
                console.log('duplicateCutElements:', err)
            });
        }
        this.socketService.emit(doc.shapes[0].imageId, SocketEvents.DuplicatedCutElements, doc);
    }

    public stackElements(doc: any) {
        console.log("STACKING : ", doc);
        this.drawingSessionService.deleteElements(doc.elementId)
        .catch(err => {
            console.log('stackElements:', err)
        });
        this.socketService.emit(doc.imageId, SocketEvents.StackedElement, doc);
    }

    public unstackElements(doc: any) {
        console.log("UNSTACKING : ", doc);
        this.drawingSessionService.addElement(doc.shape.id, doc.shape.imageId, doc.shape.author, doc.shape.properties)
        .catch(err => {
            console.log('unstackElements:', err)
        });
        
        this.socketService.emit(doc.shape.imageId, SocketEvents.UnstackedElement, doc);
    }

    // doc.elementIds should be an array containing the IDs of the shapes to select.

    public resizeCanvas(doc: any) {
        this.socketService.emit(doc.imageId, SocketEvents.ResizedCanvas, doc);
        this.databaseService.updateMultiple(Image, doc)
        .catch(err => {
            console.log('resizeCanvas:', err)
        });
    }
    
    public resetCanvas(doc: any) {
        this.socketService.emit(doc.imageId, SocketEvents.CanvasReset);
    }

    public verifyAndAct(socketId: string, doc: any, callback: (doc: any) => void) {
        if(!this.isUserLoggedIn(doc.sessionId, doc.username)) {
            this.socketService.emit(socketId, SocketEvents.UserIsNotLogged);
            return;
        }
        if (!this.isObjectSelected(doc.objectId)) { 
            this.socketService.emit(socketId, SocketEvents.ObjectIsntSelected);
            return;
        }
        else if (!this.isObjectSelectedBy(doc.sessionId, doc.objectId)) {
            this.socketService.emit(socketId, SocketEvents.ObjectSelectedByOtherUser)
        }
        callback(doc.shape);
    }

    private isUserLoggedIn(sessionId: string, username: string): Boolean {
        return this.userManager.verifySession(sessionId, username);
    }

    private isObjectSelected(objectId: string): Boolean {
        return this.selectedObjects.has(objectId);
    }

    private isObjectSelectedBy(sessionId: string, objectId: string): Boolean {
        return this.selectedObjects.get(sessionId) === objectId;
    }
    
}
