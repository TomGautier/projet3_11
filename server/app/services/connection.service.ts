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
import { UserManager } from "./user.manager";

@injectable()
export class ConnectionService implements ConnectionServiceInterface {
    
    constructor(
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
        @inject(TYPES.UserManager) private userManager: UserManager,
        @inject(TYPES.UserService) private userService: UserService,
        @inject(TYPES.SocketService) private socketService: SocketService
    ) {
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.userManager.removeUser(args[1]));
    }

    public async onUserLogin(username: string, password: string): Promise<string> {     
        const user = new User(await this.userService.find(username));
        if (user.password === password) {
            const sessionId = uuid.v1();
            this.userManager.addUser(sessionId, username);
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
        this.userManager.removeUser(username);
        await this.userService.removeByUsername(username)
//            .then(() => this.conversationManager.leaveConversation(roomId, socketId, username))
            .catch(err => Logger.warn('LoginService', `This username doesn't exist : ${username}`));
    }
}