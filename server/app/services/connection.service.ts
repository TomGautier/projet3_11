import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Logger } from "./logger.service";
import { ConversationManager } from "./conversation.manager";
import { UserService } from "./user.service";
import User from "../schemas/user"
import { ConnectionServiceInterface } from "../interfaces";
import * as uuid from 'node-uuid';

@injectable()
export class ConnectionService implements ConnectionServiceInterface {
    // The key is the username and the string the session ID.
    private connectedUsers: Map<String, String> = new Map();
    
    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService,
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
        @inject(TYPES.UserService) private userService: UserService
    ) {
    }

    public async onUserLogin(username: string, password: string): Promise<string> {     
        const user = new User(await this.userService.find(username));
        if (user.password === password) {
            const sessionId = uuid.v1();
            this.connectedUsers.set(username, sessionId)
            return sessionId;
        }
        return '';
    }

    public async onUserSignup(username: string, password: string) {   
        let createdUser = {};
        try {
            createdUser = await this.userService.create(new User({username: username, password: password}));
        }
        catch (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                Logger.warn('LoginService', `The username ${username} already exists.`);
                return false;
            }    
        }
        return createdUser ? true : false;
    }

    public async onUserDisconnection(roomId: string, socketId:string, username: string) {
        await this.userService.removeByUsername(username)
            .then(() => this.conversationManager.leaveConversation(roomId, socketId, username))
            .catch(err => Logger.warn('LoginService', `This username doesn't exist : ${username}`));
    }

    private onLoggedIn(socketId: string, username: string) {
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.onUserDisconnection(args[0], args[1][0], args[1][1]));
        this.socketService.authSocket(socketId);
        this.conversationManager.joinConversation(GENERAL_ROOM.id, socketId);
        
        this.socketService.emit(socketId, SocketEvents.UserLogged);
        Logger.info('LoginService', `The user ${username} from socket ${socketId} has been logged in.`);    
    }
}