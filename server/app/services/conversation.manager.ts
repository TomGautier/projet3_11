import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Room } from "../../../common/room";

@injectable()
export class ConversationManager {
    //private channels: Set<string>  = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], args[1][0]));
    }

    public onMessageSent(conversationId: string, message: string) {
        //date.getHours()
        this.socketService.emit(conversationId, SocketEvents.MessageSent, message);
    }

    public joinConversation(conversationId: string, socketId: string) {
        this.socketService.joinRoom(conversationId, socketId);
    }

    public leaveConversation(conversationId: string, socketId: string, username: string) {
        this.socketService.leaveRoom(conversationId, socketId, username);
    }
}
