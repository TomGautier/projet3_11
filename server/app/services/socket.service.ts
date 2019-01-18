import { injectable } from "inversify";
import { Socket } from "dgram";

@injectable()
export class SocketService {
    private server: SocketIO.Server;
    private sockets: Map<string, SocketIO.Socket> = new Map();

    public init(server: SocketIO.Server): void {
        this.server = server;
    }

}
