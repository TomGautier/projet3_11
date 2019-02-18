/* From : https://github.com/Aboisier/Projet2-Equipe6
* Written by : Dylan Farvacque
*/

const TYPES =  {
        ServerInterface: Symbol.for("ServerInterface"),
        ApplicationInterface: Symbol.for("ApplicationInterface"),
        
        // Interfaces
        ConversationControllerInterface: Symbol.for("ConversationControllerInterface"),
        ConversationServiceInterface: Symbol.for("ConversationServiceInterface"),
        MessageControllerInterface:Symbol.for("MessageControllerInterface"),
        MessageServiceInterface:Symbol.for("MessageServiceInterface"),
        
        DateControllerInterface: Symbol.for("DateControllerInterface"),
        IndexControllerInterface: Symbol.for("IndexControllerInterface"),
        IndexServiceInterface: Symbol.for("IndexServiceInterface"),
        DateServiceInterface: Symbol.for("DateServiceInterface"),

        // Services 
        EventEmitter: Symbol.for("EventEmitter"),
        SocketService: Symbol.for("SocketService"),
        DatabaseConnection: Symbol.for("DatabaseConnection"),
        DatabaseService: Symbol.for("DatabaseService"),
        ConversationService: Symbol.for("ConversationService"),
        LoginService:Symbol.for("LoginService"),
        UserService:Symbol.for("UserService"),

        // Managers
        ConversationManager:Symbol.for("ConversationManager"),
};

export  { TYPES };