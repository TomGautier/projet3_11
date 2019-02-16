import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Room } from "../../../common/room";
import { Logger } from "./logger.service";
import { ChannelsManager } from "./channels.manager";


// TODO: 
// - Find more elegant way than getting socket from socketService

@injectable()
export class LoginService {
    private users: Set<string>  = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService,
        @inject(TYPES.ChannelsManager) private channelsManager: ChannelsManager
    ) {
        this.socketService.subscribe(SocketEvents.LoginAttempt, args => this.onUserConnection(args[0], args[1][0]));
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.onUserDisconnection(args[0], args[1][0], args[1][1]));
    }

    public onUserConnection(socketId: string, username: string) {
        if (this.users.has(username)) {
            this.socketService.emit(socketId, SocketEvents.UsernameAlreadyExists);
            console.log("The username of " + socketId + " exists already.");
        }
        else {
            this.users.add(username);
            this.socketService.authSocket(socketId);
            this.channelsManager.joinChannel(GENERAL_ROOM.id, socketId);
            this.socketService.emit(socketId, SocketEvents.UserLogged);
            console.log("The socket " + socketId + " has been logged in.");
        }
    }

    public onUserDisconnection(roomId: string, socketId:string, username: string) {
        const socket = this.socketService.getSocket(socketId);
        if (socket) {
            this.channelsManager.leaveChannel(roomId, socketId, username);
            this.users.delete(username);
        }
        else {
            Logger.warn('SocketService', `This socket doesn't exist : ${socketId}`);
        }
    }
}