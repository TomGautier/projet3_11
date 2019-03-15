import { injectable, inject } from "inversify";
import { SocketService } from "./socket.service";
import { DrawingSessionService } from "./drawingSession.service";
import { TYPES } from "../types";
import SocketEvents from "../../../common/communication/socketEvents";
import { ConnectionManager } from "./connection.service";

@injectable()
export class DrawingSessionManager {
    /*If it is selected: the key is the object id and the value is the user's sessionId who selected it.
    Otherwise, it doesn't exist.*/
    private selectedObjects: Map<String, String> = new Map();

    constructor(@inject(TYPES.SocketService) private socketService: SocketService,
                @inject(TYPES.DrawingSessionServiceInterface) private drawingSessionService: DrawingSessionService,
                @inject(TYPES.ConnectionManager) private connectionManager: ConnectionManager)
               { 
        //this.drawingSessionService = new DrawingSessionService(new DatabaseService(new DatabaseConnection()));
        //this.socketService = new SocketService(new UnsaucedEventEmitter());
        // args[0] contains the socket id, args[1][0] the drawing session id.
        this.socketService.subscribe(SocketEvents.JoinDrawingSession, args => this.joinSession(args[0], args[1][0]));
        this.socketService.subscribe(SocketEvents.LeaveDrawingSession, args => this.leaveSession(args[0], args[1][0]));
        // args[0] contains the socket id, args[1] is a json with the session id, username and properties of the object.
        this.socketService.subscribe(SocketEvents.AddElement, args => this.addElement(JSON.parse(args[1][0]).shape));//this.verifyAndAct(args[0], JSON.parse(args[1][0]), this.addElement));
        this.socketService.subscribe(SocketEvents.DeleteElements, args => this.deleteElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.deleteElements));
        this.socketService.subscribe(SocketEvents.ModifyElement, args =>this.modifyElement(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));
        
        this.socketService.subscribe(SocketEvents.DuplicateElements, args => this.duplicateElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], JSON.parse(args[1][0]), this.addElement));
        this.socketService.subscribe(SocketEvents.CutElements, args => this.cutElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.deleteElements));
        
        this.socketService.subscribe(SocketEvents.StackElements, args =>this.stackElements(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));
        this.socketService.subscribe(SocketEvents.UnstackElements, args =>this.unstackElements(JSON.parse(args[1][0])));// this.verifyAndAct(args[0], args[1][0], this.modifyElement));

        this.socketService.subscribe(SocketEvents.SelectElements, args => this.selectElements(JSON.parse(args[1][0])));//this.verifyAndAct(args[0], args[1][0], this.selectElements));

        this.socketService.subscribe(SocketEvents.ResizeCanvas, args => this.verifyAndAct(args[0], args[1][0], this.resizeCanvas));
        this.socketService.subscribe(SocketEvents.ResetCanvas, args => this.verifyAndAct(args[0], args[1][0], this.resetCanvas));
    }

    public joinSession(socketId: string, sessionId: string) {
        this.socketService.joinRoom(sessionId, socketId);
    }

    public leaveSession(socketId: string, sessionId: string) {
        this.selectedObjects.forEach((value: String, key: String, map) =>
        {
            // TODO: Delete every user's selected objects when he leaves a session.
            console.log(value, key);
        });
        this.socketService.leaveRoom(sessionId, socketId);
    }

    // doc should be structured as a Shape. See: /schemas/shape.ts
    public addElement(doc: any) {
        console.log(doc);

        this.drawingSessionService.addElement(doc.drawingSessionId, doc.author, doc.properties);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.AddedElement, doc);
    }

    // doc.elementIds should be an array containing the IDs of the shapes to delete.
    public deleteElements(doc: any) {
        
        //this.drawingSessionService.deleteElements(doc.elementIds);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.DeletedElements, doc.elementIds);
    }

    // doc should be structured as a Shape. See: /schemas/shape.ts
    public modifyElement(doc: any) {
        //this.drawingSessionService.modifyElement(doc);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.ModifiedElement, doc);
    }

    public duplicateElements(doc: any) {
        console.log(doc);
        
        // FOR LOOP
        // this.drawingSessionService.addElement(doc.drawingSessionId, doc.author, doc.properties);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.DuplicatedElements, doc);
    }

    public cutElements(doc: any) {
        
        //this.drawingSessionService.deleteElements(doc.elementIds);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.CutedElements, doc.elementIds);
    }

    public stackElements(doc: any) {
        // FOR LOOP
        //this.drawingSessionService.deleteElements(doc.elementIds);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.StackedElements, doc.elementIds);
    }

    public unstackElements(doc: any) {
        
        //FOR LOOP
        //this.drawingSessionService.addElement(doc.elementIds);
        this.socketService.emit(doc.drawingSessionId, SocketEvents.UnstackedElements, doc.elementIds);
    }

    // doc.elementIds should be an array containing the IDs of the shapes to select.
    public selectElements(doc: any) {
        this.socketService.emit(doc.drawingSessionId, SocketEvents.SelectedElements, doc);
    }

    public resizeCanvas(doc: any) {
        this.socketService.emit(doc.drawingSessionId, SocketEvents.ResizedCanvas, doc);
    }
    
    public resetCanvas(doc: any) {
        this.socketService.emit(doc.drawingSessionId, SocketEvents.CanvasReset);
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
        callback(doc);
    }

    private isUserLoggedIn(sessionId: string, username: string): Boolean {
        return this.connectionManager.verifySession(sessionId, username);
    }

    private isObjectSelected(objectId: string): Boolean {
        return this.selectedObjects.has(objectId);
    }

    private isObjectSelectedBy(sessionId: string, objectId: string): Boolean {
        return this.selectedObjects.get(sessionId) === objectId;
    }
}