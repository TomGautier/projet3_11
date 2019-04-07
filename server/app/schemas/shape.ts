import { Schema } from 'mongoose';
import { IShapeModel } from '../models/shape';
import mongoose = require('mongoose');

export let shapeSchema: Schema = new Schema({
    id: {
        type: String,
        required: true
    }, 
    drawingSessionId: {
        type: String,
        required: true
    },
    author: {
        type: String,//Schema.Types.ObjectId,
        ref: 'User'
    },
    properties: {
        type: { type: String, required: true },
        fillingColor: { type: String, required: true },
        borderColor: { type: String, required: true },
        middlePointCoord: { type: [Number], required: false },
        height: { type: Number, required: true },
        width: { type: Number, required: false },
        rotation: { type: Number, required:false },
        borderType: {type: String, required:false},
        label: {type: String, required:false},
        methods: {type: [String], required:false},
        attributes: {type: [String], required:false},
        idShape1: {type: String, required:false},
        idShape2: {type: String, required:false},
        index1: {type: Number, required:false},
        index2: {type: Number, required:false},
        q1: {type: String, required:false},
        q2: {type: String, required:false},
        pointsX: {type: [Number], required:false},
        pointsY: {type: [Number], required:false},
        category: {type: String, required:false},
    },

}, {timestamps: true});

export default mongoose.connection.model<IShapeModel>('Shape', shapeSchema);

