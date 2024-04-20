#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include "org_mcmodule_silk_Native.h"
#include "SKP_Silk_SDK_API.h"

static bool CheckError(JNIEnv *jniEnv, SKP_int error) {
    if (error == SKP_SILK_NO_ERROR) return false;
    jclass class = (*jniEnv)->FindClass(jniEnv, "org/mcmodule/silk/SilkException");
    if (class != NULL) {
        jmethodID method = (*jniEnv)->GetStaticMethodID(jniEnv, class, "createFromErrorCodes", "(I)Lorg/mcmodule/silk/SilkException;");
        if (method != NULL) {
            jobject exception = (*jniEnv)->CallStaticObjectMethod(jniEnv, class, method, error);
            if (exception != NULL) {
                (*jniEnv)->Throw(jniEnv, (jthrowable) exception);
            }
        }
    }
    return true;
}

JNIEXPORT jstring JNICALL Java_org_mcmodule_silk_Native_getVersion(JNIEnv *jniEnv, jclass class) {
    return (*jniEnv)->NewStringUTF(jniEnv, SKP_Silk_SDK_get_version());
}

JNIEXPORT jint JNICALL Java_org_mcmodule_silk_Native_getEncoderSize(JNIEnv *jniEnv, jclass class) {
    SKP_int32 size;
    CheckError(jniEnv, SKP_Silk_SDK_Get_Encoder_Size(&size));
    return size;
}

JNIEXPORT void JNICALL Java_org_mcmodule_silk_Native_initEncoder(JNIEnv *jniEnv, jclass class, jlong encState, jlong encStatus) {
    SKP_SILK_SDK_EncControlStruct encControl;
    if (encStatus == 0ULL) {
        encStatus = (jlong) &encControl;
    }
    CheckError(jniEnv, SKP_Silk_SDK_InitEncoder((void *) encState, (SKP_SILK_SDK_EncControlStruct *) encStatus));
}

JNIEXPORT void JNICALL Java_org_mcmodule_silk_Native_queryEncoder(JNIEnv *jniEnv, jclass class, jlong encState, jlong encStatus) {
    CheckError(jniEnv, SKP_Silk_SDK_QueryEncoder((void *) encState, (SKP_SILK_SDK_EncControlStruct *) encStatus));
}

JNIEXPORT jbyteArray JNICALL Java_org_mcmodule_silk_Native_encode(JNIEnv *jniEnv, jclass class, jlong encState, jlong encStatus, jshortArray samplesIn, jint off, jint len) {
    SKP_SILK_SDK_EncControlStruct *encControl = (SKP_SILK_SDK_EncControlStruct *) encStatus;
    SKP_int16 length = len;
    SKP_int16 *samples = malloc(len * sizeof(SKP_int16));
    SKP_uint8 *outData = malloc(len); // encControl->bitRate / 8000 * SILK_MAX_FRAMES_PER_PACKET
    (*jniEnv)->GetShortArrayRegion(jniEnv, samplesIn, off, len, samples);
    bool error = CheckError(jniEnv, SKP_Silk_SDK_Encode((void *) encState, encControl, samples, len, outData, &length));
    jbyteArray result = NULL;
    if (!error) {
        result = (*jniEnv)->NewByteArray(jniEnv, length);
        if (result != NULL) {
            (*jniEnv)->SetByteArrayRegion(jniEnv, result, 0, length, outData);
        }
    }
    free(samples);
    free(outData);
    return result;
}

JNIEXPORT jint JNICALL Java_org_mcmodule_silk_Native_getDecoderSize(JNIEnv *jniEnv, jclass class) {
    SKP_int32 size;
    CheckError(jniEnv, SKP_Silk_SDK_Get_Decoder_Size(&size));
    return size;
}

JNIEXPORT void JNICALL Java_org_mcmodule_silk_Native_initDecoder(JNIEnv *jniEnv, jclass class, jlong decState) {
    CheckError(jniEnv, SKP_Silk_SDK_InitDecoder((void *) decState));
}

JNIEXPORT jshortArray JNICALL Java_org_mcmodule_silk_Native_decode(JNIEnv *jniEnv, jclass class, jlong decState, jlong decControl, jboolean lostFlag, jbyteArray inData, jint off, jint len) {
    SKP_SILK_SDK_DecControlStruct *control = (SKP_SILK_SDK_DecControlStruct *) decControl;
    SKP_int16 samplesLen;
    SKP_int16 *samples = malloc((control->API_sampleRate * 20) / 1000 * SILK_MAX_FRAMES_PER_PACKET * 2);
    SKP_uint8 *in = malloc(len);
    (*jniEnv)->GetByteArrayRegion(jniEnv, inData, off, len, in);
    int length = 0;
    int frames = 0;
    bool error = false;
    do {
        error = CheckError(jniEnv, SKP_Silk_SDK_Decode((void *) decState, control, lostFlag, in, len, samples + length, &samplesLen));
        length += samplesLen;
        if (++frames > SILK_MAX_FRAMES_PER_PACKET) break;
    } while(!error && control->moreInternalDecoderFrames);
    jshortArray result = NULL;
    if (!error) {
        result = (*jniEnv)->NewShortArray(jniEnv, length);
        if (result != NULL) {
            (*jniEnv)->SetShortArrayRegion(jniEnv, result, 0, length, samples);
        }
    }
    free(in);
    free(samples);
    return result;
}

JNIEXPORT jshortArray JNICALL Java_org_mcmodule_silk_Native_decodeRaw(JNIEnv *jniEnv, jclass class, jlong decState, jlong decControl, jboolean lostFlag, jbyteArray inData, jint off, jint len) {
    SKP_SILK_SDK_DecControlStruct *control = (SKP_SILK_SDK_DecControlStruct *) decControl;
    SKP_int16 samplesLen;
    SKP_int16 *samples = malloc((control->API_sampleRate * 20) / 1000 * SILK_MAX_FRAMES_PER_PACKET);
    SKP_uint8 *in = malloc(len);
    (*jniEnv)->GetByteArrayRegion(jniEnv, inData, off, len, in);
    bool error = CheckError(jniEnv, SKP_Silk_SDK_Decode((void *) decState, control, lostFlag, in, len, samples, &samplesLen));;
    jshortArray result = NULL;
    if (!error) {
        result = (*jniEnv)->NewShortArray(jniEnv, samplesLen);
        if (result != NULL) {
            (*jniEnv)->SetShortArrayRegion(jniEnv, result, 0, samplesLen, samples);
        }
    }
    free(in);
    free(samples);
    return result;
}

JNIEXPORT jbyteArray JNICALL Java_org_mcmodule_silk_Native_searchForLBRR(JNIEnv *jniEnv, jclass class, jbyteArray inData, jint off, jint len, jint lostOffset) {
    SKP_uint8 *in = malloc(len);
    SKP_uint8 *LBRRData = malloc(1024 * SILK_MAX_FRAMES_PER_PACKET);
    SKP_int16 nLBRRBytes;
    (*jniEnv)->GetByteArrayRegion(jniEnv, inData, off, len, in);
    SKP_Silk_SDK_search_for_LBRR(in, len, lostOffset, LBRRData, &nLBRRBytes);
    jbyteArray result = (*jniEnv)->NewByteArray(jniEnv, nLBRRBytes);
    if (result != NULL) {
        (*jniEnv)->SetByteArrayRegion(jniEnv, result, 0, nLBRRBytes, LBRRData);
    }
    free(in);
    free(LBRRData);
    return result;
}