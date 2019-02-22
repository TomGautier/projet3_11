import { injectable, inject } from "inversify";
import { DrawingSessionServiceInterface } from "../interfaces";
import { SocketService } from "./socket.service";
import { TYPES } from "../types";
import SocketEvents from "../../../common/communication/socketEvents";

@injectable()
export class DrawingSessionService implements DrawingSessionServiceInterface {

    constructor(@inject(TYPES.SocketService) private socketService: SocketService) { 
        this.socketService.subscribe(SocketEvents.UserJoinedSession, args => this.joinSession(args[0], args[1]));
    }

    public joinSession(socketId: string, sessionId: string) {
        this.socketService.joinRoom(sessionId, socketId);
    }

    public leaveSession(socketId: string, sessionId: string) {
        this.socketService.leaveRoom(sessionId, socketId);
    }
}