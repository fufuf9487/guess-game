import { AppPage } from './app.po';

const mockServer = require('mockttp').getLocal();

describe('App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  beforeEach(() => mockServer.start(8080));
  afterEach(() => mockServer.stop());

  it('should display welcome message', () => {
    mockServer.forGet('/api/event/default-event-part-home-info').thenReply(200, null);
    mockServer.forGet('/api/state/state').thenReply(200, '"START_STATE"');
    mockServer.forGet('/api/question/sets').thenReply(200, '[{"id":0,"name":"Question Set 1"},{"id":1,"name":"Question Set 2"},{"id":2,"name":"Question Set 3"}]');
    mockServer.forGet('/api/question/quantities').withQuery({'questionSetId': '0'}).thenReply(200, '[5,10]');

    page.navigateTo();
    expect(page.getElementAttributeValue('div', 'class')).toContain('container-home');
  });
});
