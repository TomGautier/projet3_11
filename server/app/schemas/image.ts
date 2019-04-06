import { Schema } from 'mongoose';
import { IImageModel } from '../models/image';
import mongoose = require('mongoose');

export let imageSchema: Schema = new Schema({
    id: {
        type: String,
        required: true
    }, 
    author: {
        type: String,
        ref: 'User',
        required: true
    },
    visibility: {
        type: String,
        required: true
    },
    protection: {
        type: String,
        required: true
    },
    canvasX: {
        type: Number,
        required: false
    },
    canvasY: {
        type: Number,
        required: false
    }
}, {timestamps: true});

export default mongoose.connection.model<IImageModel>('Image', imageSchema);

