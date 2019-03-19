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
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], args[1]));
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId, arg[1][0].message
        this.socketService.subscribe(SocketEvents.UserJoinedConversation, args => this.joinConversation(args[0], args[1]));
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId
        this.socketService.subscribe(SocketEvents.UserLeftConversation, args => this.leaveConversation(args[0], args[1]));
    }

    public onMessageSent(socketId: string, args: any) {
        const messageJson = {date: 'date actuelle', username: args[0].username, message: args[0].message};
        console.log(args[0]);
        this.socketService.emit(args[0].conversationId, SocketEvents.MessageSent, messageJson);
    }

    public joinConversation(socketId: string, args: any) {
        this.socketService.joinRoom(args[0].conversationId, socketId);
    }

    public leaveConversation(socketId: string, args: any) {
        this.socketService.leaveRoom(args[0].conversationId, socketId);
    }
}
