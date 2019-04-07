import { injectable, inject } from "inversify";
import { Router, Request, Response, NextFunction } from "express";

import { TYPES } from "../types";
import { UserManager } from "../services/user.manager";
import { ImageControllerInterface } from "../interfaces";
import { ImageService } from "../services/image.service";

@injectable()
export class ImageController implements ImageControllerInterface {
    
    public constructor(
        @inject(TYPES.ImageServiceInterface) private imageService: ImageService,
        @inject(TYPES.UserManager) private userManager: UserManager
    ) { }

    public get router(): Router {
        const router: Router = Router();
            
        router.get("/single/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                this.imageService.getById(req.params.imageId).then(image => {
                    res.json(image);
                });
            });

        router.get("/:sessionId/:username/:visibility",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                switch(req.params.visibility) {
                    case 'public': 
                        this.imageService.getAll().then(images => {
                            res.json(images);
                        });
                        break;
                    case 'private':
                        this.imageService.getAllByAuthor(req.params.username).then(images => {
                            res.json(images);
                        });
                        break;
                    default:
                        break;
                }
            });
/*
        router.get("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }
                
                    this.imageService.getAllByAuthor(req.params.username).then(image => {
                        res.json(image);
                    });
            });
            
        router.get("/common/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                // Send the request to the service and send the response
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username)) 
                    { res.json(403); return; }

                this.imageService.getAll().then(image => {
                    res.json(image);
                });
            }); 
*/
        router.post("/:sessionId/:username",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json(403); return; }
                
                this.imageService.create(req.body.author,
                                        req.body.id,
                                        req.body.visibility,
                                        req.body.protection)
                    .then(image => {
                        res.json(image);
                    });
            });

        router.post("/thumbnail/:sessionId/:username/:imageId",
            (req: Request, res: Response, next: NextFunction) => {
                if(!this.userManager.verifySession(req.params.sessionId, req.params.username))
                    { res.json(403); return; }
                
                this.imageService.updateThumbnail(req.params.imageId, req.body.thumbnail, req.body.thumbnailTimestamp)
                    .then(image => {
                        res.json(image);
                    });

            });

        return router;
    }
}