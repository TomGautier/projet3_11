import { injectable, inject } from "inversify";
import { Socket } from "dgram";
import { EventEmitter } from "events";
import Types from "../types";
import { Logger } from "./logger.service";

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
    }

    public emit(id: string, event: string, ...args: any[]): void {
        this.server.to(id).emit(event, args);
    }

    private handleEvent(event: string, socketId: string, ...args: any[]): void {
        this.eventEmitter.emit(event, socketId, args);
    }
}
