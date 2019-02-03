import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { Logger } from "./logger.service";
import SocketEvents from "../../../common/communication/socketEvents";
import { UnsaucedEventEmitter } from "../interfaces/events";

@injectable()
export class SocketService {
    private server: SocketIO.Server;
    private sockets: Map<string, SocketIO.Socket> = new Map();

    public constructor(
        @inject(TYPES.EventEmitter) private eventEmitter: UnsaucedEventEmitter
    ) { }

    public init(server: SocketIO.Server): void {
        this.server = server;

        this.server.on("connection", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", "New connection: " + socket.id);
            this.sockets.set(socket.id, socket);
            console.log("Socket id" + socket.id + " connected.");
        });

        this.server.on("disconnect", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", `Socket ${socket.id} left.`);
            this.handleEvent(SocketEvents.UserLeft, socket.id);
            this.sockets.delete(socket.id);

            socket.on(SocketEvents.MessageSent, args => this.handleEvent(SocketEvents.MessageSent, socket.id, args));
        });

        Logger.warn("SocketService", `Socket service initialized.`);
    }

    public emit(id: string, event: string, ...args: any[]): void {
        Logger.debug("SocketService", `Emitting ${event} to ${id}`);
        const success: boolean = this.server.to(id).emit(event, args);
        Logger.debug("SocketService", `Result of emit : ${success}`);
    }

    private handleEvent(event: string, socketId: string, ...args: any[]): void {
        Logger.debug("SocketService", `Received ${event} event from ${socketId}.`);
        this.eventEmitter.emit(event, socketId, args);
    }
}
