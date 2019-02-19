import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";

@injectable()
export class ConversationManager {
    //private channels: Set<string> = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], args[1]));
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.onMessageSent(args[0], args[1]));
    }

    public onMessageSent(conversationId: string, message: string) {
        this.socketService.emit(conversationId, SocketEvents.MessageSent, message);
    }

    public joinConversation(socketId: string, ...conversationIds: string[]) {
        for(const conversationId of conversationIds) {
            this.socketService.joinRoom(conversationId, socketId);
        }
    }

    public leaveConversation(socketId: string, ...conversationIds: string[]) {
        for(const conversationId of conversationIds) {
            this.socketService.leaveRoom(conversationId, socketId);
        }
    }
}
