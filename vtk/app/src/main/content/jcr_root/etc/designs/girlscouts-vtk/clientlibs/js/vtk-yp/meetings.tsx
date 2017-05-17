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
                {(this.props.meetings)?this.props.meetings.map((meeting, idx) => <Meeting key={meeting.id}    {...meeting} />):null}
            </div>
        );
    }
}

export default Meetings;
