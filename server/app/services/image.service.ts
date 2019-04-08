import { injectable, inject } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import { ImageServiceInterface } from "../interfaces";
import { Logger } from "./logger.service";
import Image from "../schemas/image";
import Shape from "../schemas/shape";

import * as uuid from 'node-uuid';

require('reflect-metadata');

@injectable()
export class ImageService implements ImageServiceInterface {
    private readonly ID_CRITERIA = "id";
    private readonly AUTHOR_CRITERIA = "author";
    private readonly VISIBILITY_CRITERIA = "visibility";
    private readonly PUBLIC_VISIBILITY = "public";

    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async create(imageId: string, author: string, visibility: string, protection: string): Promise<{}> {
        const image = new Image({
            id: imageId,
            author: author,
            visibility: visibility,
            protection: protection,
            canvasX: 1726,
            canvasY: 1185
        });
            
        return await this.databaseService.create(Image, image)
            .catch(err => {
                Logger.warn('ImageService', `Couldn't create image : ${imageId}`);
                throw err;
           });
    }

    public async createMultiple(shapes: any) {
        return await this.databaseService.create(Shape, shapes)
            .catch(err => {
                Logger.warn('ImageService', `Couldn't create shapes : ${shapes}`);
                throw err;
           });
    }

    public async getAllByAuthor(username: string): Promise<{}> {
        return await this.databaseService.getAllByCriteria(Image, this.AUTHOR_CRITERIA, username)
            .catch(err => {
                Logger.warn('ImageService', `This author ${username} has no images.`);
                throw err;
            });
    }

    public async getAll(): Promise<{}> {
        return await this.databaseService.getAllByCriteria(Image, this.VISIBILITY_CRITERIA, this.PUBLIC_VISIBILITY)
            .catch(err => {
                Logger.warn('ImageService', `Couldn't fetch all images.`);
                throw err;
            });
    }

    public async getById(id: string): Promise<{}> {
        return await this.databaseService.getByCriteria(Image, this.ID_CRITERIA, id)
            .catch(err => {
                Logger.warn('ImageService', `This image ${id} doesn't exist.`);
                throw err;
            });
    }

    public async removeById(id: string): Promise<{}> {
        return await this.databaseService.remove(Image, this.ID_CRITERIA, id)
            .catch(err => {
                Logger.warn('ImageService', `This image ${id} couldn't be removed.`);
                throw err;
            });
    }

    public async updateThumbnail(imageId: string, thumbnail: any, thumbnailTimestamp: number) {
        const image = new Image(await this.getById(imageId));
        if(image.thumbnailTimestamp === undefined || image.thumbnailTimestamp <= thumbnailTimestamp) {
                image.thumbnail = thumbnail;
                image.thumbnailTimestamp = thumbnailTimestamp;
                return await this.databaseService.update(Image, this.ID_CRITERIA, imageId, image)
                    .catch(err => {
                        Logger.warn('ImageService', `This image ${imageId} couldn't be updated.`);
                        throw err;
                    });
        }

        return image;
    }

    public async updateProtection(imageId: string, protection: string) {
        const doc = {
            id: imageId,
            protection: protection
        };
        try {
            await this.databaseService.updateProtection(Image, doc);
        }
        catch(err) {
            throw err;
        }
        
    }
}