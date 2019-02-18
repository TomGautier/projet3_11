/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

import { Message } from "../../common/communication/message";
import { Router } from "express";
import { Application } from "express";

export interface ConversationServiceInterface {
    getAllByUsername(username: string): Promise<{}>;
}

export interface ConversationControllerInterface {
    router: Router;
}

export interface MessageServiceInterface {
    getAllFromConversation(conversation: string): Promise<{}>;
}

export interface MessageControllerInterface {
    router: Router;
}

export interface IndexServiceInterface {
    about(): Message;
    helloWorld(): Promise<Message>;
}

export interface DateServiceInterface {
    currentTime(): Promise<Message>;
}

export interface DateControllerInterface {
    router: Router;
}

export interface IndexControllerInterface {
    router: Router;
}


export interface ApplicationInterface {
    app: Application;
    bindRoutes(): void;
}

export interface ServerInterface {
    init(): void;
}