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

    private users: Set<string>;

    public constructor(
        @inject(TYPES.EventEmitter) private eventEmitter: UnsaucedEventEmitter
    ) { }

    public init(server: SocketIO.Server): void {
        this.server = server;

        this.server.on("connection", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", "New connection: " + socket.id);
            this.sockets.set(socket.id, socket);
            console.log("Socket id" + socket.id + " connected.");

            this.joinRoom(GENERAL_ROOM.id, socket.id);

            socket.on(SocketEvents.MessageSent, args => this.handleEvent(SocketEvents.MessageSent, GENERAL_ROOM.id, args));

            this.joinRoom(GENERAL_ROOM.id, socket.id);

            socket.on(SocketEvents.LoginAttempt, args => this.handleLogin(
                                                            socket,
                                                            args[0])
                    );
            console.log("Socket " + socket.id + " now listening on LoginAttempt.");



        });

        this.server.on("disconnect", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", `Socket ${socket.id} left.`);
            this.handleEvent(SocketEvents.UserLeft, socket.id);
            this.sockets.delete(socket.id);
        });

        Logger.warn("SocketService", `Socket service initialized.`);
    }

    public subscribe(event: string, action: ((...args: any[]) => any)) {
        this.eventEmitter.on(event, action);
    }

    public joinRoom(roomId: string, ...socketIds: string[]) {
        for (const socketId of socketIds) {
            const socket = this.sockets.get(socketId);
            if (socket) {
                socket.join(roomId);
            }
            else {
                Logger.debug('SocketService', `This socket doesn't exist : ${socketId}`);
            }
        }
    }

    public leaveRoom(roomId: string, ...socketIds: string[]) {
        for (const socketId of socketIds) {
            const socket = this.sockets.get(socketId);
            if (socket) {
                socket.leave(roomId);
            }
            else {
                Logger.warn('SocketService', `This socket doesn't exist : ${socketId}`);
            }
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
            socket.on(SocketEvents.MessageSent, args => this.handleEvent(SocketEvents.MessageSent, GENERAL_ROOM.id, args[0]));
            
            this.emit(socket.id, SocketEvents.UserLogged);
            console.log("The socket " + socket.id + " has been logged in.");
        }
    }

    private handleEvent(event: string, socketId: string, args?: string): void {
        Logger.debug("SocketService", `Received ${event} event from ${socketId}.`);
        this.emit(socketId, event, args);
        // BUG: Client doesn't receive emit when using eventEmitter
        // this.eventEmitter.emit(event, socketId, args);
    }
}
