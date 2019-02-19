import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Logger } from "./logger.service";
import { ConversationManager } from "./conversation.manager";
import { UserService } from "./user.service";
import User from "../schemas/user"
import { ConnectionServiceInterface } from "../interfaces";

@injectable()
export class ConnectionService implements ConnectionServiceInterface {

    constructor(
        @inject(TYPES.SocketService) private socketService: SocketService,
        @inject(TYPES.ConversationManager) private conversationManager: ConversationManager,
        @inject(TYPES.UserService) private userService: UserService
    ) {
        // SHOULD BE OPPOSITE EVENTS + args[1][1] the password
        //this.socketService.subscribe(SocketEvents.SignUp, args => this.onUserSignup(args[0], args[1][0], args[1][1]));
        //this.socketService.subscribe(SocketEvents.LoginAttempt, args => this.onUserLogin(args[0], args[1][0], args[1][1]));
    }

    public async onUserLogin(username: string, password: string): Promise<boolean> {     
        const user = new User(await this.userService.find(username));
        return user.password === password;
        /*
            .then(() => {
                if(userDoc !== undefined) {
                    const user = new User(userDoc);
                    if (user.password === password) {
                        this.onLoggedIn(socketId, username);
                    }
                    else {
                        this.socketService.emit(socketId, SocketEvents.InvalidCredentials);
                        Logger.warn('LoginService', `Wrong credentials for ${username}.`);
                    }    
                }
                else { this.socketService.emit(socketId, SocketEvents.InvalidCredentials); }
            })
            .catch(err => {
                this.socketService.emit(socketId, SocketEvents.InvalidCredentials);
                Logger.warn('LoginService', `Could not retrieve ${username}.`);
            });*/
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