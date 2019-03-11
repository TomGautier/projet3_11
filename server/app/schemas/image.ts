import { Schema } from 'mongoose';
import { IImageModel } from '../models/Image';
import mongoose = require('mongoose');

export let imageSchema: Schema = new Schema({
    id: {
        type: Schema.Types.ObjectId,
        required: true
    }, 
    author: {
        type: String,
        ref: 'User',
    },
    visibility: {
        type: String,
        required: true
    },
    protection: {
        type: String,
        required: true
    },
    shapes: [{
        type: Schema.Types.ObjectId,
        ref: 'Shape'
    }]
}, {timestamps: true});

export default mongoose.connection.model<IImageModel>('Image', imageSchema);

