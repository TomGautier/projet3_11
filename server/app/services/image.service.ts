import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import { ImageServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";
import Image from "../schemas/image";

import * as uuid from 'node-uuid';

require('reflect-metadata');

@injectable()
export class ImageService implements ImageServiceInterface {
    private readonly ID_CRITERIA = "id";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async create(author: string, visibility: string, protection: string, shapes: string): Promise<{}> {
        const imageId = uuid.v1();
        const image =  new Image({
            id: imageId,
            author: author,
            visibility: visibility,
            protection: protection,
            shapes: shapes
        });
            
        return await this.databaseService.create(Image, image)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't create conversation : ${imageId}`);
                throw err;
           });
    }

    public async getById(id: string): Promise<{}> {
        return await this.databaseService.getByCriteria(Image, this.ID_CRITERIA, id)
            .catch(err => {
                Logger.warn('ConversationService', `This conversation ${id} doesn't exist.`);
                throw err;
            });
    }
}