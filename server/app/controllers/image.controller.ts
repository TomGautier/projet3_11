import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { ConnectionManager } from "../services/connection.service";
import { ImageControllerInterface } from "../interfaces";

@injectable()
export class ImageController implements ImageControllerInterface {
    
    public constructor(
        @inject(TYPES.ImageServiceInterface) private imageService: ImageService,
        @inject(TYPES.ConnectionManager) private connectionManager: ConnectionManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        return router;
    }
}