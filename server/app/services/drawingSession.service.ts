import { injectable, inject } from "inversify";
import { DrawingSessionServiceInterface } from "../interfaces";
import { SocketService } from "./socket.service";
import { TYPES } from "../types";
import SocketEvents from "../../../common/communication/socketEvents";

@injectable()
export class DrawingSessionService implements DrawingSessionServiceInterface {

    constructor(@inject(TYPES.SocketService) private socketService: SocketService) { 
        // args[0] contains the socket id, args[1][0] the drawing session id.
        this.socketService.subscribe(SocketEvents.JoinDrawingSession, args => this.joinSession(args[0], args[1][0]));
        // args[0] contains the socket id, args[1] is a json with the session id, username and parameters of the function.
        this.socketService.subscribe(SocketEvents.AddElement, args => this.addElement(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.DeleteElements, args => this.deleteElements(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.ModifyElement, args => this.modifyElement(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.SelectObjects, args => this.selectObject(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.UnselectObjects, args => this.unselectObjects(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.ResizeCanvas, args => this.resizeCanvas(args[0], args[1]));
    }

    public joinSession(socketId: string, sessionId: string) {
        this.socketService.joinRoom(sessionId, socketId);
    }

    public leaveSession(socketId: string, sessionId: string) {
        this.socketService.leaveRoom(sessionId, socketId);
    }

    public addElement(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.AddedElement);
    }

    public deleteElements(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.DeletedElements);
    }

    public modifyElement(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.ModifiedElement);
    }

    public selectObject(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.SelectedObjects);
    }

    public unselectObjects(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.UnselectedObjects);
    }

    public resizeCanvas(socketId: string, doc: any) {
        this.socketService.emit(doc[0].drawingSessionId, SocketEvents.ResizedCanvas);
    }
}