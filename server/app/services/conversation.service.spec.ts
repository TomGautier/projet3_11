import { assert } from 'chai';
import { IMock, Mock, It } from 'typemoq';

import { ConversationService } from './conversation.service';
import { DatabaseService } from './database.service';

describe('Password Service', () => {
    let conversationService: ConversationService;
    let databaseService: IMock<DatabaseService>;

    beforeEach(() => {
        databaseService = Mock.ofType<DatabaseService>();
        conversationService = new ConversationService(databaseService.object);
    });

    it('getByName() - Should get document provided by the DatabaseService.', async () => {
        // Arrange
        const expectedDoc = {name: 'test'};

        databaseService
            .setup(service => service.getByCriteria(It.isAny(), 'name', It.isAny()))
            .returns(() => Promise.resolve(expectedDoc));

        // Act
        const actualDoc = await conversationService.getByName('test').catch(err => console.log(err));
        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('getAllByUsername() - Should get the documents provided by the DatabaseService.', async () => {
        // Arrange
        const expectedDoc = {participants: ['test']};

        databaseService
            .setup(service => service.getAllByCriteria(It.isAny(), 'participants', It.isAny()))
            .returns(() => Promise.resolve(expectedDoc));

        // Act
        const actualDoc = await conversationService.getAllByUsername('test').catch(err => console.log(err));

        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });

    it('create() - Should create the document.', async () => {
        // Arrange
        const expectedDoc = {name: 'test', participants: ['testCreator']};

        databaseService
            .setup(service => service.create(It.isAny(), It.isAny()))
            .returns(() => Promise.resolve(expectedDoc));

        // Act
        const actualDoc = await conversationService.create('test', 'testCreator').catch(err => console.log(err));

        // Assert
        assert.equal(JSON.stringify(actualDoc), JSON.stringify(expectedDoc));
    });
});