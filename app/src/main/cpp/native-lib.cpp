//
// Created by Karasu on 1/3/2021.
//

#include <jni.h>
#include <string>
#include <iostream>

extern "C" JNIEXPORT jstring JNICALL
Java_id_ac_ui_cs_mobileprogramming_usmansidiq_shophisticated_MainActivity_getIncome(JNIEnv* env, jobject, jstring income){
    const char *incomeToday = env->GetStringUTFChars(income, NULL);
    char returnString[20];
    strcpy(returnString, "Rp.");
    strcat(returnString, incomeToday);
    env->ReleaseStringUTFChars(income, incomeToday);
    return env->NewStringUTF(returnString);
}