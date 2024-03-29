import { Schema } from 'mongoose';
import { IConversationModel } from '../models/conversation';
import mongoose = require('mongoose');

export let conversationSchema: Schema = new Schema({
    name: {
        type: String, 
        index: true,
        required: true, 
        unique: true
    },
    participants: [{ type: String, ref: 'User'}]
});

export default mongoose.connection.model<IConversationModel>('Conversation', conversationSchema);