
/* Inspired from : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Antoine Boisier-Michaud, Tom Gautier
*/

import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { Logger } from "./logger.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { UnsaucedEventEmitter } from "../interfaces/events";
import { Room } from "../../../common/room";

const GENERAL_ROOM = new Room("generalRoom")

@injectable()
export class SocketService {
    private server: SocketIO.Server;
    private sockets: Map<string, SocketIO.Socket> = new Map();
  
    private users: Set<string>  = new Set<string>();

    public constructor(
        @inject(TYPES.EventEmitter) private eventEmitter: UnsaucedEventEmitter
    ) { }

    public init(server: SocketIO.Server): void {
        this.server = server;

        this.server.on("connection", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", "New connection: " + socket.id);
            this.sockets.set(socket.id, socket);
            console.log("Socket id" + socket.id + " connected.");
            socket.on(SocketEvents.LoginAttempt, args => this.handleLogin(socket, args));
            socket.on(SocketEvents.UserLeft, args => this.leaveRoom(GENERAL_ROOM.id, socket.id, args));
            console.log("Socket " + socket.id + " now listening on LoginAttempt.");



        });

        this.server.on("disconnect", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", `Socket ${socket.id} left.`);
            //this.handleEvent(SocketEvents.UserLeft, socket.id);
            this.sockets.delete(socket.id);
            console.log("un socket a disconnect");
        });

        Logger.warn("SocketService", `Socket service initialized.`);
    }

    public subscribe(event: string, action: ((...args: any[]) => any)) {
        this.eventEmitter.on(event, action);
    }

    public joinRoom(roomId: string, socketId: string) {
        const socket = this.sockets.get(socketId);
        if (socket) {
            socket.join(roomId);
        }
        else {
            Logger.debug('SocketService', `This socket doesn't exist : ${socketId}`);
        }
        
    }

    public leaveRoom(roomId: string, socketId: string, username: string) {
        const socket = this.sockets.get(socketId);
        if (socket) {
            socket.leave(roomId);
            this.users.delete(username);
        }
        else {
            Logger.warn('SocketService', `This socket doesn't exist : ${socketId}`);
        }
    }


    public emit(id: string, event: string, args?: any): void {

        Logger.debug("SocketService", `Emitting ${event} to ${id}`);
        const success: boolean = this.server.to(id).emit(event, args);
        Logger.debug("SocketService", `Result of emit : ${success}`);
    }

    private handleLogin(socket: SocketIO.Socket, username: string) {
        if (this.users.has(username)) {
            this.emit(socket.id, SocketEvents.UsernameAlreadyExists);
            console.log("The username of " + socket.id + " exists already.");
        }
        else {
            this.users.add(username);
            this.joinRoom(GENERAL_ROOM.id, socket.id);
            socket.on(SocketEvents.MessageSent, args => this.handleEvent(SocketEvents.MessageSent, GENERAL_ROOM.id, args));
            
            this.emit(socket.id, SocketEvents.UserLogged);
            console.log("The socket " + socket.id + " has been logged in.");
        }
    }

    private handleEvent(event: string, socketId: string, args?: string): void { 
        Logger.debug("SocketService", `Received ${event} event from ${socketId}.`);
        this.emit(socketId, event, args);
    }
}