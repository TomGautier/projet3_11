/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

import { Application } from "./app";
import * as http from "http";
import * as socketIO from 'socket.io';
import { TYPES } from "./types";
import { injectable, inject } from "inversify";
import { AddressInfo } from "net";
import { ServerInterface } from "./interfaces";
import { SocketService } from "./services/socket.service";
import { DatabaseService } from "./services/database.service";

@injectable()
export class Server implements ServerInterface {

    private readonly appPort: string|number|boolean = this.normalizePort(process.env.PORT || "3000");
    private readonly baseDix: number = 10;
    private server: http.Server;

    public constructor(
        @inject(TYPES.ApplicationInterface) private application: Application,
        @inject(TYPES.SocketService) private socketService: SocketService,
        @inject(TYPES.DatabaseService) private db: DatabaseService
        ) { }

    public async init(): Promise<void> {
        this.application.app.set("port", this.appPort);

        this.server = http.createServer(this.application.app);

        this.server.listen(this.appPort);
        this.server.on("error", (error: NodeJS.ErrnoException) => this.onError(error));
        this.server.on("listening", () => this.onListening());
        
        // Inits the socket server
        this.socketService.init(socketIO.listen(this.server));

        await this.db.init();
        
        console.log('Server initialized!');
    }

    private normalizePort(val: number | string): number | string | boolean {
        const port: number = (typeof val === "string") ? parseInt(val, this.baseDix) : val;
        if (isNaN(port)) {
            return val;
        } else if (port >= 0) {
            return port;
        } else {
            return false;
        }
    }

    private onError(error: NodeJS.ErrnoException): void {
        if (error.syscall !== "listen") { throw error; }
        const bind: string = (typeof this.appPort === "string") ? "Pipe " + this.appPort : "Port " + this.appPort;
        switch (error.code) {
            case "EACCES":
                console.error(`${bind} requires elevated privileges`);
                process.exit(1);
                break;
            case "EADDRINUSE":
                console.error(`${bind} is already in use`);
                process.exit(1);
                break;
            default:
                throw error;
        }
    }

    /**
     * Se produit lorsque le serveur se met à écouter sur le port.
     */
    private  onListening(): void {
        const addr: string | AddressInfo = this.server.address();
        const bind: string = (typeof addr === "string") ? `pipe ${addr}` : `port ${addr.port}`;
        // tslint:disable-next-line:no-console
        console.log(`Listening on ${bind}`);
    }
}
