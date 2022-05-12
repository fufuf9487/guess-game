package guess.domain.source.cms.contentful.talk.response;

import guess.domain.source.cms.contentful.ContentfulResponse;
import guess.domain.source.cms.contentful.talk.ContentfulTalk;
import guess.domain.source.cms.contentful.talk.ContentfulTalkIncludes;
import guess.domain.source.cms.contentful.talk.fields.ContentfulTalkFields;

public abstract class ContentfulTalkResponse<T extends ContentfulTalkFields> extends ContentfulResponse<ContentfulTalk<T>, ContentfulTalkIncludes> {
}
