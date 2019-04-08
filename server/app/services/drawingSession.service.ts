import { DrawingSessionServiceInterface } from "../interfaces";
import Shape from "../schemas/shape";
import ShapeConnection from "../schemas/shapeConnection";
import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import { Logger } from "./logger.service";

@injectable()
export class DrawingSessionService implements DrawingSessionServiceInterface {
    private readonly ID_CRITERIA = "id";
    private readonly IMAGE_ID_CRITERIA = "imageId";
    
    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }


    public async getShapesByImage(imageId: string) {
        return await this.databaseService.getAllByCriteria(Shape, this.IMAGE_ID_CRITERIA, imageId)
            .catch(err => {
                Logger.warn('ImageService', `This image ${imageId} has no shapes.`);
                throw err;
            });
    }

    public async getShapeConnectionsByImage(imageId: string) {
        return await this.databaseService.getAllByCriteria(ShapeConnection, this.IMAGE_ID_CRITERIA, imageId)
            .catch(err => {
                Logger.warn('ImageService', `This image ${imageId} has no shape connection.`);
                throw err;
            });
    }

    public async addElement(id:string, imageId: string, username: string, properties: any): Promise<{}> {
        const shape = new Shape({
            id: id,
            imageId: imageId,
            author: username,
            properties: {
                type: properties.type,
                fillingColor: properties.fillingColor,
                borderColor: properties.borderColor,
                strokeType: properties.strokeType,
                middlePointCoord: properties.middlePointCoord,
                height: properties.height,
                width: properties.width,
                rotation: properties.rotation,
                borderType:properties.borderType,
                label:properties.label,
                methods:properties.methods,
                attributes:properties.attributes,
                idShape1:properties.idShape1,
                idShape2:properties.idShape2,
                index1:properties.index1,
                index2:properties.index2,
                q1:properties.q1,
                q2:properties.q2,
                points:properties.points,
                category:properties.category
            }
        });
    
        return await this.databaseService.create(Shape, shape)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't create shape : ${id}`);
                throw err;
        });
    }
    
    public async deleteElements(elementIds: any): Promise<{}> {
        // TODO: Review criteria to remove every elements
        const elementsWithCriteria = {$in: elementIds};
        return await this.databaseService.remove(Shape, this.ID_CRITERIA, elementsWithCriteria)
            .catch(err => {throw err;});
    }

    public async modifyElement(element: any): Promise<{}> {
        // TODO: Might have to transform element into a Shape + reassigning same id?
        return await this.databaseService.update(Shape, this.ID_CRITERIA, element.id, element)
            .catch(err => {throw err;});
    }
}