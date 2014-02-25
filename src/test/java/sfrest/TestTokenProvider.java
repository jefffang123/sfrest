package sfrest;

import static sfrest.Environment.PRODUCTION;

class TestTokenProvider extends UserPassTokenProvider {

    TestTokenProvider() {
        setEnvironment(PRODUCTION);
        setClientId("3MVG9Y6d_Btp4xp5Xqs8.5xmFm2lAaZDOz2aeLy6mH.p6RXoshrl1SMWhsDoF10Fwi.cVo92zI.RKQguP0bUc");
        setClientSecret("781889688054271860");
        setUsername("sanlyfang@gmail.com");
        setPassword("test1234");
        setSecurityToken("9hCzimEBARhsnCxhKpeqdaQBX");
    }
}
