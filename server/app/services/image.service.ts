import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import { ImageServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";

require('reflect-metadata');

@injectable()
export class ImageService implements ImageServiceInterface {

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

}