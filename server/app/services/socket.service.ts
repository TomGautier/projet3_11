
/* Inspired from : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Antoine Boisier-Michaud, Tom Gautier
*/

import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { Logger } from "./logger.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { UnsaucedEventEmitter } from "../interfaces/events";
import { Room } from "../../../common/room";

export const GENERAL_ROOM = new Room("General")

@injectable()
export class SocketService {
    private server: SocketIO.Server;
    private sockets: Map<string, SocketIO.Socket> = new Map();

    public getSocket(socketId: string): SocketIO.Socket | undefined {
        return this.sockets.get(socketId);
    }

    public constructor(
        @inject(TYPES.EventEmitter) private eventEmitter: UnsaucedEventEmitter
    ) {
    }
    
    public init(server: SocketIO.Server): void {
        this.server = server;

        this.server.on("connection", (socket: SocketIO.Socket) => {
            this.sockets.set(socket.id, socket);
            console.log("Socket id", socket.id, "connected.");
            socket.on(SocketEvents.LoginAttempt, args => this.handleEvent(SocketEvents.LoginAttempt, socket.id, args));
            socket.on(SocketEvents.UserLeft, args => this.handleEvent(SocketEvents.UserLeft, socket.id, args));
            socket.on(SocketEvents.MessageSent, args => this.handleEvent(SocketEvents.MessageSent, socket.id, args));
            socket.on(SocketEvents.UserJoinedConversation, args => this.handleEvent(SocketEvents.UserJoinedConversation, socket.id, args));

            socket.on(SocketEvents.JoinDrawingSession, args => this.handleEvent(SocketEvents.JoinDrawingSession, socket.id, args));

            socket.on(SocketEvents.AddElement, args => this.handleEvent(SocketEvents.AddElement, socket.id, args));
            socket.on(SocketEvents.DeleteElements, args => this.handleEvent(SocketEvents.DeleteElements, socket.id, args));
            socket.on(SocketEvents.ModifyElement, args => this.handleEvent(SocketEvents.ModifyElement, socket.id, args));

            socket.on(SocketEvents.DuplicateElements, args => this.handleEvent(SocketEvents.DuplicateElements, socket.id, args));
            socket.on(SocketEvents.CutElements, args => this.handleEvent(SocketEvents.CutElements, socket.id, args));
            socket.on(SocketEvents.DuplicateCutElements, args => this.handleEvent(SocketEvents.DuplicateCutElements, socket.id, args));

            socket.on(SocketEvents.StackElement, args => this.handleEvent(SocketEvents.StackElement, socket.id, args));
            socket.on(SocketEvents.UnstackElement, args => this.handleEvent(SocketEvents.UnstackElement, socket.id, args));

            socket.on(SocketEvents.SelectElements, args => this.handleEvent(SocketEvents.SelectElements, socket.id, args));
            socket.on(SocketEvents.ResizeCanvas, args => this.handleEvent(SocketEvents.ResizeCanvas, socket.id, args));
            socket.on(SocketEvents.ResetCanvas, args => this.handleEvent(SocketEvents.ResetCanvas, socket.id, args));
            Logger.debug("SocketService", "New connection: " + socket.id);
        });

        this.server.on("disconnect", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", `Socket ${socket.id} left.`);
            socket.disconnect();
            this.handleEvent(SocketEvents.UserLeft, socket.id);
            this.sockets.delete(socket.id);
        });

        Logger.debug("SocketService", `Socket service initialized.`);
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

    public leaveRoom(roomId: string, socketId: string) {
        const socket = this.sockets.get(socketId);
        if (socket) {
            socket.leave(roomId);
        }
        else {
            Logger.warn('SocketService', `This socket doesn't exist : ${socketId}`);
        }
    }

    public emit(id: string, event: string, args?: any): void {
        Logger.debug("SocketService", `Emitting ${event} to ${id}`);
        const success: boolean = this.server.to(id).emit(event, args);
        console.log("Socket Emit a ", id);
        Logger.debug("SocketService", `Result of emit : ${success}`);
    }

    public broadcast(event: string, args?: any): void {
        Logger.debug("SocketService", `Broadcasting ${event}`);
        const success = this.server.emit(event, args);
        Logger.debug("SocketService", `Result of emit : ${success}`);
    }

    private handleEvent(event: string, socketId: string, ...args: string[]): void {
        Logger.debug("SocketService", `Received ${event} event from ${socketId}.`);
        this.eventEmitter.emit(event, socketId, args);
    }
}
