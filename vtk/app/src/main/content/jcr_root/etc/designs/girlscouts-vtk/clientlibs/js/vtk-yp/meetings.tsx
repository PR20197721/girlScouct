import * as React from 'react';

import Meeting from './meeting';

interface MeetingsProps {
    meetings:any[]
};

interface MeetingsState {};

class Meetings extends React.Component < MeetingsProps,
    MeetingsState> {
    
    public render() : JSX.Element {
        return (
            <div className="list-meetings">
                {(this.props.meetings.length)?this.props.meetings.map((meeting, idx) => <Meeting key={meeting.meetingInfo.position+'-meeting-'+idx}  idx={idx}  {...meeting.meetingInfo} />):null}
            </div>
        );
    }
}

export default Meetings;
