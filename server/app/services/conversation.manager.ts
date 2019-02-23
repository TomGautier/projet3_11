import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { SocketService } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";

@injectable()
export class ConversationManager {
    //private channels: Set<string> = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], args[1][0]));
        // arg [0] socketId, arg[1][0] sessionId, arg[1][1] username, arg[1][2] conversationName
        this.socketService.subscribe(SocketEvents.UserJoinedConversation, args => this.joinConversation(args[0], args[1][0], args[1][1], args[1][2]));
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.onMessageSent(args[0], args[1]));
    }

    public onMessageSent(conversationId: string, message: string) {
<<<<<<< Updated upstream
        console.log('conversationId:', conversationId, message);
=======
        console.log("passe dans onMessageSent");
>>>>>>> Stashed changes
        this.socketService.emit(conversationId, SocketEvents.MessageSent, message);
    }

    public joinConversation(socketId: string, sessionId: string, username: string, conversationId: string) {
        this.socketService.joinRoom(conversationId, socketId);
    }

    public leaveConversation(socketId: string, ...conversationIds: string[]) {
        for(const conversationId of conversationIds) {
            this.socketService.leaveRoom(conversationId, socketId);
        }
    }
}
