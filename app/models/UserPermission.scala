package models

sealed trait UserPermission
case object AdministratorPermission extends UserPermission
case object SchoolPermission extends UserPermission
case object StudentPermission extends UserPermission
case object TeacherPermission extends UserPermission

object UserPermission {
  def valueOf(value: String): UserPermission = value match {
    case "Administrator" => AdministratorPermission
    case "Normal" => StudentPermission
    case "School" => SchoolPermission
    case "Teacher" => TeacherPermission
    case _ => throw new IllegalArgumentException()
  }
  def stringify(value: UserPermission): String = value match {
    case AdministratorPermission => "Administrator"
    case StudentPermission => "Normal"
    case TeacherPermission => "Teacher"
    case SchoolPermission => "School"
    case _ => throw new IllegalArgumentException()
  }
}