package se.inera.statistics.service;

public class HSAServiceImpl implements HSAService {
    @Override
    public HSAInfo getHSAInfo(HSAKey key) {
        return new HSAInfo("nationell", null);
    }
}
