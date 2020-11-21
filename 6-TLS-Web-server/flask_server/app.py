from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager
# from . import db

app = Flask(__name__)
db = SQLAlchemy()

if __name__ == '__main__':
    print("main")
    app.config['SECRET_KEY'] = 'thisismysecretkeydonotstealit'
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.sqlite3'

    db.init_app(app)
    print("main")
    login_manager = LoginManager()
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)

    from models import User
    # from models import User

    @login_manager.user_loader
    def load_user(user_id):
        return User.query.get(int(user_id))

    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint)

    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    app.run(host="0.0.0.0", port=80, debug=True)