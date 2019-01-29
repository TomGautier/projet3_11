import { injectable, inject } from "inversify";
import { EventEmitter } from "events";
import Types from "../types";
import { Logger } from "./logger.service";
import SocketEvents from "../../common/communication/socketEvents";

@injectable()
export class SocketService {
    private server: SocketIO.Server;
    private sockets: Map<string, SocketIO.Socket> = new Map();

    public constructor(
        @inject(Types.EventEmitter) private eventEmitter: EventEmitter
    ) { }

    public init(server: SocketIO.Server): void {
        this.server = server;

        this.server.on("connection", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", "New connection: " + socket.id);
            this.sockets.set(socket.id, socket);
        });

        this.server.on("disconnect", (socket: SocketIO.Socket) => {
            Logger.debug("SocketService", `Socket ${socket.id} left.`);
            this.handleEvent(SocketEvents.UserLeft, socket.id);
            this.sockets.delete(socket.id);
        });

        Logger.debug("SocketService", "Socket server initialized");
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
