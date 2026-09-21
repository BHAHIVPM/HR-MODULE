package com.bhahi.hrmodule.Utils;

import com.bhahi.hr.exception.CustomException;
import com.bhahi.hrmodule.model.auth.UserLogin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Single place for the one-way user-creation hierarchy (no reverse, no skips except ADMIN):
 *   DEVELOPER -> SUPERADMIN -> ADMIN -> (USER / EMPLOYEE / AGENT)
 * Only DEVELOPER can create SUPERADMIN, only SUPERADMIN can create ADMIN,
 * only ADMIN can create USER / EMPLOYEE / AGENT.
 * USER / EMPLOYEE / AGENT cannot create anyone.
 */
public final class UserRolePolicy {

    private UserRolePolicy() {
    }

    public static UserLogin.UserType requireCreatableTarget(UserLogin.UserType target) {
        if (target == null) {
            throw new CustomException("User type missing.", "Please select a user type (SUPERADMIN / ADMIN / USER / EMPLOYEE / AGENT).", 400);
        }
        Authentication auth = SecurityContextHolder.getContext() == null
                ? null : SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new CustomException("Unauthorized.", "Login is required to create a user.", 401);
        }
        UserLogin.UserType creator = resolveCreatorType(auth);
        if (creator == null) {
            throw new CustomException("Forbidden.", "Your account role is not recognised, so you cannot create users.", 403);
        }
        if (!canCreate(creator, target)) {
            throw new CustomException("Forbidden.",
                    creator.name() + " cannot create " + target.name()
                            + ". Allowed: DEVELOPER -> SUPERADMIN -> ADMIN -> (USER / EMPLOYEE / AGENT).",
                    403);
        }
        return target;
    }

    /** EMPLOYEE creation produces an EMPLOYEE record/login, so only an ADMIN can call it. */
    public static void requireEmployeeCreationAllowed() {
        Authentication auth = SecurityContextHolder.getContext() == null
                ? null : SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new CustomException("Unauthorized.", "Login is required to create an employee.", 401);
        }
        UserLogin.UserType creator = resolveCreatorType(auth);
        if (creator == null) {
            throw new CustomException("Forbidden.", "Your account role is not recognised, so you cannot create employees.", 403);
        }
        if (!canCreate(creator, UserLogin.UserType.EMPLOYEE)) {
            throw new CustomException("Forbidden.",
                    creator.name() + " cannot create EMPLOYEE. Only ADMIN can create USER / EMPLOYEE / AGENT.", 403);
        }
    }

    public static boolean canCreate(UserLogin.UserType creator, UserLogin.UserType target) {
        if (creator == null || target == null) {
            return false;
        }
        switch (creator) {
            case DEVELOPER:
                return target == UserLogin.UserType.SUPERADMIN;
            case SUPERADMIN:
                return target == UserLogin.UserType.ADMIN;
            case ADMIN:
                return target == UserLogin.UserType.USER
                        || target == UserLogin.UserType.EMPLOYEE
                        || target == UserLogin.UserType.AGENT;
            case USER:
            case EMPLOYEE:
            case AGENT:
            default:
                return false;
        }
    }

    private static UserLogin.UserType resolveCreatorType(Authentication auth) {
        // Prefer the granted authority (JwtFilter stores the real UserType there, e.g. ADMIN).
        if (auth.getAuthorities() != null) {
            for (GrantedAuthority ga : auth.getAuthorities()) {
                String role = ga == null ? null : ga.getAuthority();
                if (role == null) {
                    continue;
                }
                // tolerate ROLE_ prefix if some config ever adds it
                String clean = role.startsWith("ROLE_") ? role.substring(5) : role;
                try {
                    return UserLogin.UserType.valueOf(clean);
                } catch (IllegalArgumentException ignored) {
                    // try next authority
                }
            }
        }
        return null;
    }
}
