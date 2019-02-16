import { Schema } from 'mongoose';
import { IConversationModel } from '../models/conversation';
import mongoose = require('mongoose');

export let conversationSchema: Schema = new Schema({
    participants: [{ type: Schema.Types.ObjectId, ref: 'User'}]
});

export default mongoose.connection.model<IConversationModel>('Conversation', conversationSchema);