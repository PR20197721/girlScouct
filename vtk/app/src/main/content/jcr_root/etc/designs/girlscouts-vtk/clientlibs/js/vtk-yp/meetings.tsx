import * as React from 'react';

import Meeting from './meeting';

interface MeetingsProps {
    meetings:any[]
};

interface MeetingsState {};

class Meetings extends React.Component < MeetingsProps,
    MeetingsState> {
    


    public render() : JSX.Element {
    

    
        let juan = [
            {
                outdoor: true,
                id: "D16TC01",
                cat: "3 Cheers for Animals",
                name: "Birdbath Award pt. 1",
                blurb: "Daisies find out how to care for animals.",
                level: "gstest",
                position: 1
            },
             {
                outdoor:false,
                id: "D16TC02",
                cat: "3 Cheers for Animals",
                name: "Birdbath Award pt. 1",
                blurb: "Daisies find out how to care for animals.",
                level: "gstest",
                position: 2
            },
             {
                outdoor: true,
                id: "D16TC03",
                cat: "3 Cheers for Animals",
                name: "Birdbath Award pt. 1",
                blurb: "Daisies find out how to care for animals.",
                level: "gstest",
                position: 3
            } 
        ];

        return (
            <div className="list-meetings">
                {juan.map((meeting, idx) => <Meeting key={meeting.id}    {...meeting} />)}
            </div>
        );
    }
}

export default Meetings;
