// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

var db = admin.firestore();

exports.onGroupMemberChanged = functions.firestore
    .document('groups/{groupid}/members/{userid}')
    .onUpdate((event) => {
        const newValue = event.data.data();
        const oldValue = event.data.previous.data();

        if(newValue == oldValue) return;

        var ref = db.collection('recruits').doc(event.params.userid).collection('groups').doc(event.params.groupid);
        return ref.update({
            member: {
                recruitReference: newValue.recruitReference,
                role: newValue.role,
                state: newValue.state
            }
        });
});

exports.onGroupMemberDeleted = functions.firestore
    .document('groups/{groupid}/members/{userid}')
    .onDelete((event) => {
        var ref = db.collection('recruits').doc(event.params.userid).collection('groups').doc(event.params.groupid);
        return ref.delete();
});

exports.onRecruitGroupChanged = functions.firestore
    .document('recruits/{recruitid}/groups/{groupid}')
    .onUpdate((event) => {
        const newValue = event.data.data();
        const oldValue = event.data.previous.data();

        if(newValue == oldValue) return;

        var ref = db.collection('groups').doc(event.params.groupid).collection('members').doc(event.params.recruitid);
        return ref.update({
            recruitReference: newValue.member.recruitReference,
            role: newValue.member.role,
            state: newValue.member.state
        });
});

exports.onRecruitGroupDeleted = functions.firestore
    .document('recruits/{recruitid}/groups/{groupid}')
    .onDelete((event) => {
    var ref = db.collection('groups').doc(event.params.groupid).collection('members').doc(event.params.recruitid);
    return ref.delete();
});

