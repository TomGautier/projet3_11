import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Room } from "../../../common/room";
import { Logger } from "./logger.service";
import { ConversationManager } from "./conversation.manager";
import { UserService } from "./user.service";


// TODO: 
// - Find more elegant way than getting socket from socketService

@injectable()
export class LoginService {
    private users: Set<string>  = new Set<string>();

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService,
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
        @inject(TYPES.UserService) private userService: UserService
    ) {
        this.socketService.subscribe(SocketEvents.LoginAttempt, args => this.onUserConnection(args[0], args[1][0]));
    }

    public async onUserConnection(socketId: string, username: string) {
        try {
            // TEMPORAL UNTIL FRONT END SENDS THE RIGHT INFO.
            const user = { 
                username: username,
                password: 'basicpwd' 
            };
            await this.userService.create(user);

            this.socketService.subscribe(SocketEvents.UserLeft, args => this.onUserDisconnection(args[0], args[1][0], args[1][1]));
            this.socketService.authSocket(socketId);
            this.conversationManager.joinConversation(GENERAL_ROOM.id, socketId);
            
            this.socketService.emit(socketId, SocketEvents.UserLogged);
            Logger.info('LoginService', `The user ${username} from socket ${socketId} has been logged in.`);
        }
        catch (err) {
            this.socketService.emit(socketId, SocketEvents.UsernameAlreadyExists);
            Logger.warn('LoginService', `The username ${username} already exists.`);
        }
    }

    public async onUserDisconnection(roomId: string, socketId:string, username: string) {
        const socket = this.socketService.getSocket(socketId);
        if (socket) {
            try {
                await this.userService.removeByUsername(username);
                this.conversationManager.leaveConversation(roomId, socketId, username);
            }
            catch (err) {
                Logger.warn('LoginService', `This username doesn't exist : ${username}`);
            }
        }
        else {
            Logger.warn('LoginService', `This socket doesn't exist : ${socketId}`);
        }
    }
}