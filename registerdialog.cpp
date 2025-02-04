#include "registerdialog.h"
#include "global.h"
#include "ui_registerdialog.h"

#include "httpmanager.h"
#include <qjsondocument.h>

RegisterDialog::RegisterDialog(QWidget *parent)
    : QDialog(parent)
    , ui(new Ui::RegisterDialog)
{
    ui->setupUi(this);

    ui->password_edit->setEchoMode(QLineEdit::Password);
    ui->confirm_edit->setEchoMode(QLineEdit::Password);

    ui->err_tip->setProperty("state", "normal");
    repolish(ui->err_tip);

    connect(HttpManager::getInstance().get(), &HttpManager::sig_register_mod_finish,
            this, &RegisterDialog::slot_register_mod_finish);

    initHttpHandles();
}

RegisterDialog::~RegisterDialog()
{
    delete ui;
}

void RegisterDialog::on_get_code_btn_clicked()
{
    auto email = ui->email_edit->text();
    QRegularExpression regex(R"((^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$))");

    bool isMatch = regex.match(email).hasMatch();
    if (isMatch) {
        // 发送http验证码
    }
    else {
        showTip(tr("邮箱地址不合法"), false);
    }
}

void RegisterDialog::showTip(QString str, bool b_ok)
{
    if (b_ok) {
        ui->err_tip->setProperty("state", "normal");
    }
    else {
        ui->err_tip->setProperty("state", "err");
    }
    ui->err_tip->setText(str);
    repolish(ui->err_tip);
}

void RegisterDialog::slot_register_mod_finish(RequestId id, QString res, ErrorCodes err)
{
    if (err != ErrorCodes::SUCCESS) {
        showTip(tr("网络请求错误"), false);
        return;
    }

    // 解析json字符串，res转化为QByteArray
    QJsonDocument jsonDoc = QJsonDocument::fromJson(res.toUtf8());
    if (jsonDoc.isNull()) {
        showTip(tr("json解析失败"), false);
        return;
    }

    // json解析错误
    if (jsonDoc.isObject()) {
        showTip(tr("json解析失败"), false);
        return;
    }

    _handlers[id](jsonDoc.object());
    return;
}

void RegisterDialog::initHttpHandles()
{
    // 注册获取验证码回调的逻辑
    _handlers.insert(RequestId::ID_GET_VARIFY_CODE, [this](const QJsonObject& jsonObject){
        int error = jsonObject["error"].toInt();
        if (error != ErrorCodes::SUCCESS) {
            showTip(tr("参数错误"), false);
            return;
        }

        auto email = jsonObject["email"].toString();
        showTip(tr("验证码已发送到邮箱，请注意查收！"), false);
        qDebug() << "email is " << email;
    });
}
