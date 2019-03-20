import { injectable, inject } from "inversify";
import { SocketService } from "./socket.service";
import { TYPES } from "../types";
import SocketEvents from "../../../common/communication/socketEvents";

@injectable()
export class UserManager {
    // The key is the username and the 2nd field is the session ID.
    private connectedUsers: Map<String, String> = new Map();

    public constructor(@inject(TYPES.SocketService) private socketService: SocketService) { 
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.removeUser(args[1]));
    }

    public addUser(sessionId: string, username: string) {
        console.log('addUser', sessionId, username);
        this.connectedUsers.set(username, sessionId);
        console.log("addedUser:", sessionId, username, this.connectedUsers.get(username));
        this.socketService.broadcast(SocketEvents.UserJoinedChat, username);
    }

    public removeUser(username: string) {
        this.connectedUsers.delete(username);
    }

    public isConnected(username: string): boolean {
        return this.connectedUsers.has(username);
    } 

    public verifySession(sessionId: string, username: string) : boolean {
        console.log("ConnectedUsers:", sessionId, username, this.connectedUsers.get(username));
        return this.connectedUsers.get(username) === sessionId;
    }
}