import { DrawingSessionServiceInterface } from "../interfaces";
import Shape from "../schemas/shape";
import { inject, injectable } from "inversify";
import { TYPES } from "../types";
import { DatabaseService } from "./database.service";
import { Logger } from "./logger.service";
import * as uuid from 'node-uuid';

@injectable()
export class DrawingSessionService implements DrawingSessionServiceInterface {
    private readonly ID_CRITERIA = "id";
    constructor(@inject(TYPES.DatabaseService) private databaseService: DatabaseService) {
    }

    public async addElement(id:string, drawingSessionId: string, username: string, properties: any): Promise<{}> {
        const shapeId = id//uuid.v1();
        const shape = new Shape({
            id: shapeId,
            drawingSessionId: drawingSessionId,
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
                label: properties.label,
                attributes: properties.attributes,
                methods: properties.methods
            }
        });
    
        return await this.databaseService.create(Shape, shape)
            .catch(err => {
                Logger.warn('ConversationService', `Couldn't create shape : ${shapeId}`);
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