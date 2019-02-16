import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Room } from "../../../common/room";

@injectable()
export class ChannelsManager {
    //private channels: Set<string>  = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.MessageSent, args => this.onMessageSent(args[0], args[1][0]));
    }

    public onMessageSent(channelId: string, message: string) {
        this.socketService.emit(channelId, SocketEvents.MessageSent, message);
    }

    public joinChannel(roomId: string, socketId: string) {
        this.socketService.joinRoom(roomId, socketId);
    }

    public leaveChannel(roomId: string, socketId: string, username: string) {
        this.socketService.leaveRoom(roomId, socketId, username);
    }
}
