package withyuu.tutorial.java11to17.record;

import withyuu.tutorial.java11to17.record.annotation.OnField;
import withyuu.tutorial.java11to17.record.annotation.OnFieldAndParameter;
import withyuu.tutorial.java11to17.record.annotation.OnMethod;
import withyuu.tutorial.java11to17.record.annotation.OnParameter;
import withyuu.tutorial.java11to17.record.annotation.OnType;
import withyuu.tutorial.java11to17.record.annotation.OnTypeUse;

@OnType
public record TestSubject(
    @OnParameter int p1,
    @OnMethod int p2,
    @OnField int p3,
    @OnFieldAndParameter int p4,
    @OnTypeUse int p5) {
}
