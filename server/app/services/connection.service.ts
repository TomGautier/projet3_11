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
import user from "../schemas/user";

@injectable()
export class ConnectionManager {
    // The key is the username and the 2nd field is the session ID.
    private connectedUsers: Map<String, String> = new Map();

    public addUser(sessionId: string, username: string) {
        console.log("added user ",username + "with sessionId ",sessionId);
        this.connectedUsers.set(username, sessionId);
    }

    public removeUser(username: string) {
        this.connectedUsers.delete(username);
    }

    public verifySession(sessionId: string, username: string) : boolean {
        return this.connectedUsers.get(username) === sessionId;
    }
}

@injectable()
export class ConnectionService implements ConnectionServiceInterface {
    
    constructor(
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
        @inject(TYPES.ConnectionManager) private connectionManager: ConnectionManager,
        @inject(TYPES.UserService) private userService: UserService,
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.connectionManager.removeUser(args[1]));
    }

    public async onUserLogin(username: string, password: string): Promise<string> {     
        const user = new User(await this.userService.find(username));
        if (user.password === password) {
            const sessionId = uuid.v1();
            this.connectionManager.addUser(sessionId, username);
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
        this.connectionManager.removeUser(username);
        await this.userService.removeByUsername(username)
//            .then(() => this.conversationManager.leaveConversation(roomId, socketId, username))
            .catch(err => Logger.warn('LoginService', `This username doesn't exist : ${username}`));
    }
}