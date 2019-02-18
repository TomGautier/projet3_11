import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { SocketService, GENERAL_ROOM } from "./socket.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { Room } from "../../../common/room";
import { Logger } from "./logger.service";
import { ConversationManager } from "./conversation.manager";
import { UserService } from "./user.service";
import User from "../schemas/user"

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
        // SHOULD BE OPPOSITE EVENTS + args[1][1] the password
        this.socketService.subscribe(SocketEvents.LoginAttempt, args => this.onUserSignup(args[0], args[1][0]));
        this.socketService.subscribe(SocketEvents.SignUp, args => this.onUserConnection(args[0], args[1][0]));
    }

    public async onUserSignup(socketId: string, username: string) {   
        // TEMPORAL UNTIL FRONT END SENDS THE RIGHT PASSWORD.
        const user = { 
            username: username,
            password: 'basicpwd' 
        };
        
        await this.userService.create(user)
            .then(() => this.onLoggedIn(socketId, username))
            .catch(err => {
                if (err.name === 'MongoError' && err.code === 11000) {
                    // Duplicate username
                    this.socketService.emit(socketId, SocketEvents.UsernameAlreadyExists);
                    Logger.warn('LoginService', `The username ${username} already exists.`);
                }    
        });
    }

    public async onUserConnection(socketId: string, username: string) {     
        const userDoc = await this.userService.find(username)
            .then(() => {
                if(userDoc !== undefined) {
                    const user = new User(userDoc);
                    if (user.password === 'basicpwd') {
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
            });
    }

    public async onUserDisconnection(roomId: string, socketId:string, username: string) {
        const socket = this.socketService.getSocket(socketId);
        if (socket) {
            await this.userService.removeByUsername(username)
                .then(() => this.conversationManager.leaveConversation(roomId, socketId, username))
                .catch(err => Logger.warn('LoginService', `This username doesn't exist : ${username}`));
        }
        else {
            Logger.warn('LoginService', `This socket doesn't exist : ${socketId}`);
        }
    }

    private onLoggedIn(socketId: string, username: string) {
        this.socketService.subscribe(SocketEvents.UserLeft, args => this.onUserDisconnection(args[0], args[1][0], args[1][1]));
        this.socketService.authSocket(socketId);
        this.conversationManager.joinConversation(GENERAL_ROOM.id, socketId);
        
        this.socketService.emit(socketId, SocketEvents.UserLogged);
        Logger.info('LoginService', `The user ${username} from socket ${socketId} has been logged in.`);    
    }

    private verifyPassword(expectedPassword: string, enteredPassword: string) {
        console.log(expectedPassword, enteredPassword);
    }
}