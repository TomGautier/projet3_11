import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { SocketService } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { ConversationService } from "./conversation.service";

@injectable()
export class ConversationManager {
    //private channels: Set<string> = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], JSON.parse(args[1])));
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId, arg[1][0].message
        this.socketService.subscribe(SocketEvents.UserJoinedConversation, args => this.joinConversation(args[0], JSON.parse(args[1])));
        // arg [0] socketId, arg[1][0].sessionId, arg[1][0].username, arg[1][0].conversationId
        this.socketService.subscribe(SocketEvents.UserLeftConversation, args => this.leaveConversation(args[0], JSON.parse(args[1])));

        this.socketService.subscribe(SocketEvents.InviteToConversation, args => this.inviteToConversation(args[0], JSON.parse(args[1])));
        this.socketService.subscribe(SocketEvents.RespondToConversationInvite, args => this.respondToConversationInvite(args[0], JSON.parse(args[1])));
    }

    public onMessageSent(socketId: string, args: any) {
        const d = Date.now();
        const date = (new Date(Date.now())).toLocaleTimeString();
        
        const messageJson = {date: date, username: args.username, message: args.message};
        this.socketService.emit(args.conversationId, SocketEvents.MessageSent, messageJson);
    }

    public joinConversation(socketId: string, args: any) {
        this.socketService.joinRoom(args.conversationId, socketId);
        this.socketService.emit(args.conversationId, SocketEvents.UserJoinedConversation, args.username);
    }

    public leaveConversation(socketId: string, args: any) {
        this.socketService.leaveRoom(args.conversationId, socketId);
        this.socketService.emit(args.conversationId, SocketEvents.UserLeftConversation, args.username);
    }

    public inviteToConversation(socketId: string, args: any) {
        const doc = { username: args.username, invitedUsername: args.invitedUsername, conversationId: args.conversationId };
        const invitedSocketId = this.socketService.getUserSocketId(args.invitedUsername);
        console.log(invitedSocketId);
        if(invitedSocketId !== undefined) {
            this.socketService.emit(invitedSocketId, SocketEvents.InvitedToConversation, doc)
        }
        else {
            this.socketService.emit(socketId, SocketEvents.UserIsNotConnected)
        }
    }

    public respondToConversationInvite(socketId: string, args: any) { 
        const doc = { username: args.username, invitedUsername: args.invitedUsername, conversationId: args.conversationId, response: args.response };
        const invitingSocketId = this.socketService.getUserSocketId(args.username);
        if(invitingSocketId !== undefined) {
            this.socketService.emit(invitingSocketId, SocketEvents.RespondedToConversationInvite, doc)
        }
        else {
            this.socketService.emit(socketId, SocketEvents.UserIsNotConnected)
        }
    }
}
