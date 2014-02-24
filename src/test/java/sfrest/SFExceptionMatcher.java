package sfrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class SFExceptionMatcher extends TypeSafeMatcher<SFException> {

    static SFExceptionMatcher INVALID_ClIENT_ID = new SFExceptionMatcher("invalid_client_id");
    static SFExceptionMatcher INVALID_CLIENT = new SFExceptionMatcher("invalid_client");
    static SFExceptionMatcher INVALID_GRANT = new SFExceptionMatcher("invalid_grant");

    private String expectedErrorCode;

    private SFExceptionMatcher(String expectedErrorCode) {
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(describe(expectedErrorCode));
    }

    @Override
    protected boolean matchesSafely(SFException item) {
        return expectedErrorCode.equalsIgnoreCase(item.getErrorCode());
    }

    @Override
    protected void describeMismatchSafely(SFException item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendText(describe(item.getErrorCode()));
    }

    private String describe(String errorCode) {
        return String.format("SFException['%s']", errorCode);
    }
}
