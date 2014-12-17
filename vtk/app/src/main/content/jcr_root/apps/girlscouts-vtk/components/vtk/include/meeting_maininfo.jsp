<section className="column large-20 medium-20 large-centered medium-centered" id="main-info">
  <div className="row">
    <div className="column large-17 medium-17 small-17">
      <p>{this.props.blurb}</p>
      <section>
        <p>Location:</p>
        <p>{this.props.location}</p>
      </section>
      <section>
        <p>Category:</p>
        <p>{this.props.cat}</p>
      </section>
    </div>
    <div className="column large-7 medium-7 small-7">
      <img src={this.props.meetingGlobalId} alt="badge" />
    </div>
  </div>
</section>
