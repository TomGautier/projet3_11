/* Inspired from : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Tom Gautier
*/

import { injectable, inject } from "inversify";
import { TYPES } from "../types";

import mongoose = require("mongoose");
import { Logger } from "./logger.service";
require("reflect-metadata");

@injectable()
export class DatabaseConnection {
    private isInit: boolean;

    public async connect(connectionString: string) {
        if (this.isInit) { return; }

        await new Promise(resolve => {
            mongoose.connect(connectionString, { useNewUrlParser: true });

            mongoose.connection.on("connected", () => {
                Logger.debug('DatabaseService', 'Mongoose default connection opened.');
                this.isInit = true;
                resolve();
            });

            mongoose.connection.on("error", (err: any) => { throw err; });

            mongoose.connection.on("disconnected", () => {
                Logger.debug("DatabaseService", "Mongoose default connection ended.");
            });
        });
    }

    public async disconnect() {
        await new Promise(resolve => {
            mongoose.disconnect((err: any) => {
                if (err) { throw err; }
                this.isInit = false;
                resolve();
            });
        });
    }
}

@injectable()
export class DatabaseService {
    private readonly MONGODB_CONNECTION = "mongodb://root:password1@ds054999.mlab.com:54999/projet3_11";

    constructor(@inject(TYPES.DatabaseConnection) private databaseConnection: DatabaseConnection) { }

    public async init() {
        await this.databaseConnection.connect(this.MONGODB_CONNECTION);
    }

    public async create(model: any, doc: any): Promise<{}> {
        return new Promise((resolve, reject) => {
            model.create(doc, (err: any, document: any) => {
                if (err) { throw err; };
                resolve(document);
            });
        });
    }

    public async delete(model: any, doc: any): Promise<{}> {
        return new Promise((resolve, reject) => {
            model.remove({ 'id': doc }, (err: any, document: any) => {
                if (err) { throw err; };
                resolve(document);
            });
        });
    }
}